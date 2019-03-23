# Seinfeld As a Service - Stage 1

This is a basic walkthrough of several AWS services in order create a service about nothing.

## Getting Started

This is the first step in the walkthrough.

You'll find a CloudFormation template (`cloudformation/start.yaml`) and the initial SQL to create the tables and import data (`sql/seinfeld.sql.tgz`).

### Prerequisites

You'll need the following things installed on your machine to complete this section:

* An AWS Account.
* The [AWS CLI](https://docs.aws.amazon.com/cli/latest/userguide/cli-chap-install.html).
* A [MySQL CLI](https://dev.mysql.com/doc/mysql-installation-excerpt/5.7/en/).
 * A Docker container running `mysql:latest` would also work.
* A text editor, I recommend [Vim](https://www.vim.org/).
* [Git](https://git-scm.com/) to download and browse through this repository locally.

## Stage Overview

In this stage you will:
* Login to your AWS account and create an IAM user with credentials for programmatic access.
* Create your first CloudFormation stack, using the supplied template (`cloudformation/start.yaml`)
* Create a HostedZone in Route53.
* Create a MySQL database in RDS.
* Import the existing data (`sql/seinfeld.sql.tgz`) into your new database.
