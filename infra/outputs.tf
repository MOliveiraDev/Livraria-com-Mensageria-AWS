output "sns_topics" {
  value = { for key, topic in aws_sns_topic.topics : key => topic.arn }
}

output "sqs_queues" {
  value = { for key, queue in aws_sqs_queue.queues : key => queue.url }
}
