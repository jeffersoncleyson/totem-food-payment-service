AWS_PROFILE=localstack
ENDPOINT=http://localhost:4566
SNS_ARN=arn:aws:sns:us-east-1:000000000000:payment-topic

MESSAGE="{\"id\": 1,
\"order\": \"65c9884c0f02af12cc47ed1f\",
\"customer\": \"12585824659\",
\"price\": 15,
\"status\": \"CANCELED\",
\"modifiedAt\": \"2024-02-12T03:17:02Z\",
\"createAt\": \"2024-02-12T03:15:06Z\"
}"

MESSAGE_PARSED=$(echo "$MESSAGE" | tr -d '\n')

ATTRIBUTES="{
\"id\": { \"DataType\": \"String\", \"StringValue\": \"a029a154-8dd2-d887-5431-48048086c167\" },
\"order\": { \"DataType\": \"String\", \"StringValue\": \"UPDATE\" },
\"timestamp\": { \"DataType\": \"Number.java.lang.Long\", \"StringValue\": \"1686074750503\" }
}"

ATTRIBUTES_PARSED=$(echo "$ATTRIBUTES" | tr -d '\n')

aws sns publish --profile "$AWS_PROFILE" --endpoint-url "$ENDPOINT" --topic-arn "$SNS_ARN" --message "$MESSAGE_PARSED" --message-attributes "$ATTRIBUTES_PARSED"