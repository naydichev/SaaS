# Seinfeld As a Service

This is a basic walkthrough of several AWS services in order create a service about nothing.

## Getting Started

The following sections detail the process of getting the final product up and running. Feel free
to skip ahead to whichever relevant sections below.

### Prerequisites

You'll need the following things installed on your machine to complete this section:

* An AWS Account.
* The [AWS CLI](https://docs.aws.amazon.com/cli/latest/userguide/cli-chap-install.html).
* A [MySQL CLI](https://dev.mysql.com/doc/mysql-installation-excerpt/5.7/en/).
 * A Docker container running `mysql:latest` would also work.
* A text editor, I recommend [Vim](https://www.vim.org/).
* [Git](https://git-scm.com/) to download and browse through this repository locally.

## Overview

The following sections will cover the different stages of the sLab.

### Stage 1

The goal for this stage is for you to be able to login to your account, create a new user, and an initial CloudFormation stack.

* Login to your AWS account and create an IAM user with credentials for programmatic access.
 * If your account has been made for you, then you'll need to reset your password to get access via the `root` user.
 * You should then create an IAM user by going to the IAM console. You'll want to enable `programmatic access` and attach the `Administrator` policy.
* Create your first CloudFormation stack, using the supplied template (`cloudformation/start.yaml`)
 * Using the CloudFormation console, you'll want to choose `Create Stack`.
 * Make sure that `Template is ready` is selected, and then proceed to choose `Upload a template file` and upload `cloudformation/start.yaml`.
 * On the following screen, you'll be given an opportunity to fill in some parameters necessary for creating the stack. Fill these in with reasonable values.
 * Proceed to create the stack and wait for the various resources to be created.
* The template will create the following resources for you:
 * A HostedZone in Route53.
 * A MySQL database in RDS.
* In order to complete the setup of the zone, please provide me with the name of your HostedZone and the NS records for your HostedZone (Slack preferred).
* Import the existing data (`sql/seinfeld.sql.tgz`) into your new database.
 * ```bash
   zcat sql/seinfeld.sql.tgz | mysql -h $RDS_HOST -u $USER -p $DB_NAME
   ```

### Stage 2

The goal for this stage is to create an SSL certificate with AWS Certificate Manager.

* Due to a requirement that we'll talk about later, we'll need to make this as a separate CloudFormation stack.
 * The resource that we're trying to create is as follows:
  ```yaml
  sLabCertificate:
    Type: AWS::CertificateManager::Certificate
    Properties:
      DomainName: !Sub
        - "${Prefix}.saas.slab.sytac.dev"
        - { Prefix: !Ref HostedZonePrefix }
      ValidationMethod: DNS
  ```
 * You can use the provided template in `cloudformation/stage-2-acm.yaml` or create your own.
* We'll use the AWS CLI to update the template this time.
  ```bash
  aws --region us-east-1 cloudformation create-stack --stack-name "$STACK_NAME" --template-body "$(cat cloudformation/stage-2-acm.yaml)" --parameters ParameterKey=HostedZonePrefix,ParameterValue=$HOSTED_ZONE_PREFIX
  ```
  * Make sure to replace `$STACK_NAME` with a new stack name, and `$HOSTED_ZONE_PREFIX` with the same value you used to create the first CloudFormation stack.
* For DNS Validation, we'll need to create a new DNS record in our HostedZone. If we go to the ACM service in the Console, we can have ACM create this for us automatically.
 * An alternative would be to make this ourselves in the Route53 console:
  * Go to the Route53 console and choose your HostedZone. You'll want to click on `Create Record Set` on the top of the page, just about in the middle.
  * On the right column, enter in the prefix for the CNAME. This is everything before `<prefix>.saas.slab.sytac.dev`, and then choose `CNAME` for the `Type`.
  * In the value, you'll enter the long string that ends in `.acm-validation.aws.` (the final dot is important).
  * Finally, click `Create` at the bottom.
* For now, we can basically ignore the certificate. It will take several minutes, but the validation should resolve on its own.
 * Keep checking it periodically to make sure it completes, and let me know if not so that we can address it.

### Stage 3
