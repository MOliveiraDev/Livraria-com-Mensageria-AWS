output "sns_topics" {
  description = "SNS Topics ARNs"
  value = {
    livro_criado       = aws_sns_topic.livro_criado.arn
    livro_atualizado   = aws_sns_topic.livro_atualizado.arn
    livro_alugado      = aws_sns_topic.livro_alugado.arn
    rental_reminder    = aws_sns_topic.rental_reminder.arn
    book_returned      = aws_sns_topic.book_returned.arn
  }
}

output "sqs_queues" {
  description = "SQS Queues URLs"
  value = {
    catalog_livro_alugado      = aws_sqs_queue.livro_alugado_queue_catalog.url
    catalog_book_returned      = aws_sqs_queue.book_returned_queue.url
    rental_livro_criado        = aws_sqs_queue.livro_criado_queue.url
    rental_livro_atualizado    = aws_sqs_queue.livro_atualizado_queue.url
    notification_livro_alugado = aws_sqs_queue.livro_alugado_queue_notification.url
    notification_rental_reminder = aws_sqs_queue.rental_reminder_queue.url
  }
}
