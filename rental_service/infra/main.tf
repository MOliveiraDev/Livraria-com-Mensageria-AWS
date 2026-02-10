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

# SQS Queue - Livro Criado (Consumer)
resource "aws_sqs_queue" "livro_criado_queue" {
  name = "livro-criado-queue"
  
  tags = {
    Environment = var.environment
    Service     = "rental-service"
  }
}

# SQS Queue - Livro Atualizado (Consumer)
resource "aws_sqs_queue" "livro_atualizado_queue" {
  name = "livro-atualizado-queue"
  
  tags = {
    Environment = var.environment
    Service     = "rental-service"
  }
}

# SNS Topic - Livro Alugado (Producer)
resource "aws_sns_topic" "livro_alugado" {
  name = "livro-alugado-topic"
  
  tags = {
    Environment = var.environment
    Service     = "rental-service"
  }
}

# SQS Queue Policy - Livro Criado
resource "aws_sqs_queue_policy" "livro_criado_policy" {
  queue_url = aws_sqs_queue.livro_criado_queue.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Principal = "*"
        Action = "sqs:SendMessage"
        Resource = aws_sqs_queue.livro_criado_queue.arn
        Condition = {
          ArnEquals = {
            "aws:SourceArn" = "arn:aws:sns:${var.aws_region}:${var.aws_account_id}:livro-criado-topic"
          }
        }
      }
    ]
  })
}

# SQS Queue Policy - Livro Atualizado
resource "aws_sqs_queue_policy" "livro_atualizado_policy" {
  queue_url = aws_sqs_queue.livro_atualizado_queue.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Principal = "*"
        Action = "sqs:SendMessage"
        Resource = aws_sqs_queue.livro_atualizado_queue.arn
        Condition = {
          ArnEquals = {
            "aws:SourceArn" = "arn:aws:sns:${var.aws_region}:${var.aws_account_id}:livro-atualizado-topic"
          }
        }
      }
    ]
  })
}

# SNS Subscription - Livro Criado -> SQS
resource "aws_sns_topic_subscription" "livro_criado_subscription" {
  topic_arn = "arn:aws:sns:${var.aws_region}:${var.aws_account_id}:livro-criado-topic"
  protocol  = "sqs"
  endpoint  = aws_sqs_queue.livro_criado_queue.arn
}

# SNS Subscription - Livro Atualizado -> SQS
resource "aws_sns_topic_subscription" "livro_atualizado_subscription" {
  topic_arn = "arn:aws:sns:${var.aws_region}:${var.aws_account_id}:livro-atualizado-topic"
  protocol  = "sqs"
  endpoint  = aws_sqs_queue.livro_atualizado_queue.arn
}
