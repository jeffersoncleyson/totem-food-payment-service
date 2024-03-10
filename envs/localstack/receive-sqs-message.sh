QUEUE_NAME=$1

# shellcheck disable=SC1046
# shellcheck disable=SC1073
if [ -z "$QUEUE_NAME" ]
then
	echo "Missing Queue name arg"
	return
fi



AWS_PROFILE=localstack
ENDPOINT=http://localhost:4566
QUEUE_URL=http://localhost:4566/000000000000/$QUEUE_NAME
NUMBER_OF_MESSAGES=10

MESSAGE_RECEIVED=$(aws sqs receive-message --profile "$AWS_PROFILE" --endpoint-url "$ENDPOINT" --queue-url "$QUEUE_URL" --max-number-of-messages $NUMBER_OF_MESSAGES)

echo "$MESSAGE_RECEIVED" | jq -r '.Messages[].Body ' | jq '{Message: .Message, Attributes: .MessageAttributes}' | jq .

RECEIPT_HANDLE=$(echo "$MESSAGE_RECEIVED" | jq '.Messages[].ReceiptHandle' | jq -s .)
for row in $(echo "${RECEIPT_HANDLE}" | jq -r '.[]'); do
  echo -e "Deleting ReceiptHandle: $row"
  aws sqs delete-message --profile "$AWS_PROFILE" --endpoint-url "$ENDPOINT" --queue-url "$QUEUE_URL" --receipt-handle "$row"
done
