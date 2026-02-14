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

# ==================== SNS TOPICS ====================

# Catalog Service - Topics
resource "aws_sns_topic" "livro_criado" {
  name = "livro-criado-topic"
  tags = {
    Environment = var.environment
    Service     = "catalog-service"
  }
}

resource "aws_sns_topic" "livro_atualizado" {
  name = "livro-atualizado-topic"
  tags = {
    Environment = var.environment
    Service     = "catalog-service"
  }
}

# Rental Service - Topics
resource "aws_sns_topic" "livro_alugado" {
  name = "livro-alugado-topic"
  tags = {
    Environment = var.environment
    Service     = "rental-service"
  }
}

resource "aws_sns_topic" "rental_reminder" {
  name = "rental-reminder-topic"
  tags = {
    Environment = var.environment
    Service     = "rental-service"
  }
}

resource "aws_sns_topic" "book_returned" {
  name = "book-returned-topic"
  tags = {
    Environment = var.environment
    Service     = "rental-service"
  }
}

# ==================== SQS QUEUES ====================

# Catalog Service - Queues
resource "aws_sqs_queue" "livro_alugado_queue_catalog" {
  name = "livro-alugado-queue"
  tags = {
    Environment = var.environment
    Service     = "catalog-service"
  }
}

resource "aws_sqs_queue" "book_returned_queue" {
  name = "book-returned-queue"
  tags = {
    Environment = var.environment
    Service     = "catalog-service"
  }
}

# Rental Service - Queues
resource "aws_sqs_queue" "livro_criado_queue" {
  name = "livro-criado-queue"
  tags = {
    Environment = var.environment
    Service     = "rental-service"
  }
}

resource "aws_sqs_queue" "livro_atualizado_queue" {
  name = "livro-atualizado-queue"
  tags = {
    Environment = var.environment
    Service     = "rental-service"
  }
}

# Notification Service - Queues
resource "aws_sqs_queue" "livro_alugado_queue_notification" {
  name = "notification-livro-alugado-queue"
  tags = {
    Environment = var.environment
    Service     = "notification-service"
  }
}

resource "aws_sqs_queue" "rental_reminder_queue" {
  name = "rental-reminder-queue"
  tags = {
    Environment = var.environment
    Service     = "notification-service"
  }
}

# ==================== SQS POLICIES ====================

resource "aws_sqs_queue_policy" "livro_alugado_catalog_policy" {
  queue_url = aws_sqs_queue.livro_alugado_queue_catalog.id
  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Effect    = "Allow"
      Principal = "*"
      Action    = "sqs:SendMessage"
      Resource  = aws_sqs_queue.livro_alugado_queue_catalog.arn
      Condition = {
        ArnEquals = { "aws:SourceArn" = aws_sns_topic.livro_alugado.arn }
      }
    }]
  })
}

resource "aws_sqs_queue_policy" "book_returned_policy" {
  queue_url = aws_sqs_queue.book_returned_queue.id
  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Effect    = "Allow"
      Principal = "*"
      Action    = "sqs:SendMessage"
      Resource  = aws_sqs_queue.book_returned_queue.arn
      Condition = {
        ArnEquals = { "aws:SourceArn" = aws_sns_topic.book_returned.arn }
      }
    }]
  })
}

resource "aws_sqs_queue_policy" "livro_criado_policy" {
  queue_url = aws_sqs_queue.livro_criado_queue.id
  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Effect    = "Allow"
      Principal = "*"
      Action    = "sqs:SendMessage"
      Resource  = aws_sqs_queue.livro_criado_queue.arn
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
      Effect    = "Allow"
      Principal = "*"
      Action    = "sqs:SendMessage"
      Resource  = aws_sqs_queue.livro_atualizado_queue.arn
      Condition = {
        ArnEquals = { "aws:SourceArn" = aws_sns_topic.livro_atualizado.arn }
      }
    }]
  })
}

resource "aws_sqs_queue_policy" "livro_alugado_notification_policy" {
  queue_url = aws_sqs_queue.livro_alugado_queue_notification.id
  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Effect    = "Allow"
      Principal = "*"
      Action    = "sqs:SendMessage"
      Resource  = aws_sqs_queue.livro_alugado_queue_notification.arn
      Condition = {
        ArnEquals = { "aws:SourceArn" = aws_sns_topic.livro_alugado.arn }
      }
    }]
  })
}

resource "aws_sqs_queue_policy" "rental_reminder_policy" {
  queue_url = aws_sqs_queue.rental_reminder_queue.id
  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Effect    = "Allow"
      Principal = "*"
      Action    = "sqs:SendMessage"
      Resource  = aws_sqs_queue.rental_reminder_queue.arn
      Condition = {
        ArnEquals = { "aws:SourceArn" = aws_sns_topic.rental_reminder.arn }
      }
    }]
  })
}

# ==================== SNS SUBSCRIPTIONS ====================

resource "aws_sns_topic_subscription" "livro_alugado_to_catalog" {
  topic_arn = aws_sns_topic.livro_alugado.arn
  protocol  = "sqs"
  endpoint  = aws_sqs_queue.livro_alugado_queue_catalog.arn
}

resource "aws_sns_topic_subscription" "livro_alugado_to_notification" {
  topic_arn = aws_sns_topic.livro_alugado.arn
  protocol  = "sqs"
  endpoint  = aws_sqs_queue.livro_alugado_queue_notification.arn
}

resource "aws_sns_topic_subscription" "book_returned_to_catalog" {
  topic_arn = aws_sns_topic.book_returned.arn
  protocol  = "sqs"
  endpoint  = aws_sqs_queue.book_returned_queue.arn
}

resource "aws_sns_topic_subscription" "livro_criado_to_rental" {
  topic_arn = aws_sns_topic.livro_criado.arn
  protocol  = "sqs"
  endpoint  = aws_sqs_queue.livro_criado_queue.arn
}

resource "aws_sns_topic_subscription" "livro_atualizado_to_rental" {
  topic_arn = aws_sns_topic.livro_atualizado.arn
  protocol  = "sqs"
  endpoint  = aws_sqs_queue.livro_atualizado_queue.arn
}

resource "aws_sns_topic_subscription" "rental_reminder_to_notification" {
  topic_arn = aws_sns_topic.rental_reminder.arn
  protocol  = "sqs"
  endpoint  = aws_sqs_queue.rental_reminder_queue.arn
}
