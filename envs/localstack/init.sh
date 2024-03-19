#!/bin/bash

aws configure set aws_access_key_id access_key_aws_local --profile default
aws configure set aws_secret_access_key secret_key_aws_local --profile default
aws configure set default.region us-east-1 --profile default

################## SNS
awslocal sns create-topic --name payment-topic
awslocal sns create-topic --name email-topic

################## SQS
awslocal sqs create-queue --queue-name create-payment-queue
awslocal sqs create-queue --queue-name payment-update-order-queue
awslocal sqs create-queue --queue-name email-queue

################## SNS SUBSCRIBE SQS
SUBSCRIPTION_ARN_CREATE_PAYMENT=$(awslocal sns subscribe --topic-arn arn:aws:sns:us-east-1:000000000000:payment-topic \
--protocol sqs \
--notification-endpoint http://localhost:4566/000000000000/create-payment-queue \
--return-subscription-arn \
--output text)

awslocal sns set-subscription-attributes \
    --subscription-arn "$SUBSCRIPTION_ARN_CREATE_PAYMENT" \
    --attribute-name FilterPolicy \
    --attribute-value "{ \"status\": \"WAITING_PAYMENT\" }"

##
awslocal sns subscribe --topic-arn arn:aws:sns:us-east-1:000000000000:email-topic \
--protocol sqs \
--notification-endpoint http://localhost:4566/000000000000/email-queue

##
SUBSCRIPTION_ARN_PAYMENT_UPDATE_ORDER=$(awslocal sns subscribe --topic-arn arn:aws:sns:us-east-1:000000000000:payment-topic \
--protocol sqs \
--notification-endpoint http://localhost:4566/000000000000/payment-update-order-queue \
--return-subscription-arn \
--output text)

awslocal sns set-subscription-attributes \
    --subscription-arn "$SUBSCRIPTION_ARN_PAYMENT_UPDATE_ORDER" \
    --attribute-name FilterPolicy \
    --attribute-value "{ \"order\": \"UPDATE\" }"

