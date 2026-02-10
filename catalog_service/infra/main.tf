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

# SNS Topic - Livro Criado
resource "aws_sns_topic" "livro_criado" {
  name = "livro-criado-topic"
  
  tags = {
    Environment = var.environment
    Service     = "catalog-service"
  }
}

# SNS Topic - Livro Atualizado
resource "aws_sns_topic" "livro_atualizado" {
  name = "livro-atualizado-topic"
  
  tags = {
    Environment = var.environment
    Service     = "catalog-service"
  }
}

# SQS Queue - Livro Alugado (Consumer)
resource "aws_sqs_queue" "livro_alugado_queue" {
  name = "livro-alugado-queue"
  
  tags = {
    Environment = var.environment
    Service     = "catalog-service"
  }
}

# SQS Queue Policy - Livro Alugado
resource "aws_sqs_queue_policy" "livro_alugado_policy" {
  queue_url = aws_sqs_queue.livro_alugado_queue.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Principal = "*"
        Action = "sqs:SendMessage"
        Resource = aws_sqs_queue.livro_alugado_queue.arn
        Condition = {
          ArnEquals = {
            "aws:SourceArn" = "arn:aws:sns:${var.aws_region}:${var.aws_account_id}:livro-alugado-topic"
          }
        }
      }
    ]
  })
}

# SNS Subscription - Livro Alugado -> SQS
resource "aws_sns_topic_subscription" "livro_alugado_subscription" {
  topic_arn = "arn:aws:sns:${var.aws_region}:${var.aws_account_id}:livro-alugado-topic"
  protocol  = "sqs"
  endpoint  = aws_sqs_queue.livro_alugado_queue.arn
}