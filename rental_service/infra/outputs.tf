output "livro_criado_queue_url" {
  value = aws_sqs_queue.livro_criado_queue.url
}

output "livro_atualizado_queue_url" {
  value = aws_sqs_queue.livro_atualizado_queue.url
}

output "livro_alugado_topic_arn" {
  value = aws_sns_topic.livro_alugado.arn
}
