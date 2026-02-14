terraform {
  required_version = ">= 1.0"
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

provider "aws" {
  region = var.aws_region
}

# SNS Topics
resource "aws_sns_topic" "livro_criado" {
  name = "livro-criado-topic"
}

resource "aws_sns_topic" "livro_atualizado" {
  name = "livro-atualizado-topic"
}

resource "aws_sns_topic" "livro_alugado" {
  name = "livro-alugado-topic"
}

resource "aws_sns_topic" "livro_lembrete" {
  name = "livro-lembrete-topic"
}

resource "aws_sns_topic" "livro_retornado" {
  name = "livro-retornado-topic"
}

# SQS Queues
resource "aws_sqs_queue" "livro_alugado_queue" {
  name = "livro-alugado-queue"
}

resource "aws_sqs_queue" "livro_retornado_queue" {
  name = "livro-retornado-queue"
}

resource "aws_sqs_queue" "livro_lembrete_queue" {
  name = "livro-lembrete-queue"
}

resource "aws_sqs_queue" "livro_criado_queue" {
  name = "livro-criado-queue"
}

resource "aws_sqs_queue" "livro_atualizado_queue" {
  name = "livro-atualizado-queue"
}

# SQS Policies
resource "aws_sqs_queue_policy" "livro_alugado_policy" {
  queue_url = aws_sqs_queue.livro_alugado_queue.id
  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Effect = "Allow"
      Principal = "*"
      Action = "sqs:SendMessage"
      Resource = aws_sqs_queue.livro_alugado_queue.arn
      Condition = {
        ArnEquals = { "aws:SourceArn" = aws_sns_topic.livro_alugado.arn }
      }
    }]
  })
}

resource "aws_sqs_queue_policy" "livro_retornado_policy" {
  queue_url = aws_sqs_queue.livro_retornado_queue.id
  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Effect = "Allow"
      Principal = "*"
      Action = "sqs:SendMessage"
      Resource = aws_sqs_queue.livro_retornado_queue.arn
      Condition = {
        ArnEquals = { "aws:SourceArn" = aws_sns_topic.livro_retornado.arn }
      }
    }]
  })
}

resource "aws_sqs_queue_policy" "livro_lembrete_policy" {
  queue_url = aws_sqs_queue.livro_lembrete_queue.id
  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Effect = "Allow"
      Principal = "*"
      Action = "sqs:SendMessage"
      Resource = aws_sqs_queue.livro_lembrete_queue.arn
      Condition = {
        ArnEquals = { "aws:SourceArn" = aws_sns_topic.livro_lembrete.arn }
      }
    }]
  })
}

resource "aws_sqs_queue_policy" "livro_criado_policy" {
  queue_url = aws_sqs_queue.livro_criado_queue.id
  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Effect = "Allow"
      Principal = "*"
      Action = "sqs:SendMessage"
      Resource = aws_sqs_queue.livro_criado_queue.arn
      Condition = {
        ArnEquals = { "aws:SourceArn" = aws_sns_topic.livro_criado.arn }
      }
    }]
  })
}

resource "aws_sqs_queue_policy" "livro_atualizado_policy" {
  queue_url = aws_sqs_queue.livro_atualizado_queue.id
  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Effect = "Allow"
      Principal = "*"
      Action = "sqs:SendMessage"
      Resource = aws_sqs_queue.livro_atualizado_queue.arn
      Condition = {
        ArnEquals = { "aws:SourceArn" = aws_sns_topic.livro_atualizado.arn }
      }
    }]
  })
}

# SNS Subscriptions
resource "aws_sns_topic_subscription" "livro_alugado_sub" {
  topic_arn = aws_sns_topic.livro_alugado.arn
  protocol  = "sqs"
  endpoint  = aws_sqs_queue.livro_alugado_queue.arn
}

resource "aws_sns_topic_subscription" "livro_retornado_sub" {
  topic_arn = aws_sns_topic.livro_retornado.arn
  protocol  = "sqs"
  endpoint  = aws_sqs_queue.livro_retornado_queue.arn
}

resource "aws_sns_topic_subscription" "livro_lembrete_sub" {
  topic_arn = aws_sns_topic.livro_lembrete.arn
  protocol  = "sqs"
  endpoint  = aws_sqs_queue.livro_lembrete_queue.arn
}

resource "aws_sns_topic_subscription" "livro_criado_sub" {
  topic_arn = aws_sns_topic.livro_criado.arn
  protocol  = "sqs"
  endpoint  = aws_sqs_queue.livro_criado_queue.arn
}

resource "aws_sns_topic_subscription" "livro_atualizado_sub" {
  topic_arn = aws_sns_topic.livro_atualizado.arn
  protocol  = "sqs"
  endpoint  = aws_sqs_queue.livro_atualizado_queue.arn
}
