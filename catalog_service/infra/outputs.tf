output "livro_criado_topic_arn" {
  description = "ARN do tópico SNS para livro criado"
  value       = aws_sns_topic.livro_criado.arn
}

output "livro_atualizado_topic_arn" {
  description = "ARN do tópico SNS para livro atualizado"
  value       = aws_sns_topic.livro_atualizado.arn
}

output "livro_alugado_queue_url" {
  description = "URL da fila SQS para livro alugado"
  value       = aws_sqs_queue.livro_alugado_queue.url
}