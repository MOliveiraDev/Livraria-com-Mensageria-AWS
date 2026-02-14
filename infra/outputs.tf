output "sns_topics" {
  value = {
    livro_criado     = aws_sns_topic.livro_criado.arn
    livro_atualizado = aws_sns_topic.livro_atualizado.arn
    livro_alugado    = aws_sns_topic.livro_alugado.arn
    livro_lembrete   = aws_sns_topic.livro_lembrete.arn
    livro_retornado  = aws_sns_topic.livro_retornado.arn
  }
}

output "sqs_queues" {
  value = {
    livro_alugado_queue    = aws_sqs_queue.livro_alugado_queue.url
    livro_retornado_queue  = aws_sqs_queue.livro_retornado_queue.url
    livro_lembrete_queue   = aws_sqs_queue.livro_lembrete_queue.url
    livro_criado_queue     = aws_sqs_queue.livro_criado_queue.url
    livro_atualizado_queue = aws_sqs_queue.livro_atualizado_queue.url
  }
}
