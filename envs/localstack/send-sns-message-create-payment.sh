AWS_PROFILE=localstack
ENDPOINT=http://localhost:4566
SNS_ARN=arn:aws:sns:us-east-1:000000000000:payment-topic

MESSAGE="{
  \"order\": \"xyz\"
}"

ATTRIBUTES="{
    \"id\": { \"DataType\": \"String\", \"StringValue\": \"a029a154-8dd2-d887-5431-48048086c167\" },
    \"status\": { \"DataType\": \"String\", \"StringValue\": \"WAITING_PAYMENT\" },
    \"timestamp\": { \"DataType\": \"Number.java.lang.Long\", \"StringValue\": \"1686074750503\" }
}"

aws sns publish --profile "$AWS_PROFILE" --endpoint-url "$ENDPOINT" --topic-arn "$SNS_ARN" --message "$MESSAGE" --message-attributes "$ATTRIBUTES"