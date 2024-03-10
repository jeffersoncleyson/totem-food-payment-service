AWS_PROFILE=localstack
ENDPOINT=http://localhost:4566

aws sqs list-queues --profile "$AWS_PROFILE" --endpoint-url "$ENDPOINT" --output text