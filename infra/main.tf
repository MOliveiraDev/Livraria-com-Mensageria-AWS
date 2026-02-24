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

locals {
  sns_topics = {
    livro_criado               = "livro-criado-topic"
    livro_atualizado           = "livro-atualizado-topic"
    livro_alugado              = "livro-alugado-topic"
    livro_retornado            = "livro-retornado-topic"
    livro_lembrete_notification = "livro-lembrete-notification-topic"
  }

  sqs_queues = {
    livro_criado_queue_notification = {
      name      = "livro-criado-queue-notification"
      topic_key = "livro_criado"
    }
    livro_criado_queue_catalog = {
      name      = "livro-criado-queue-catalog"
      topic_key = "livro_criado"
    }
    livro_atualizado_queue_notification = {
      name      = "livro-atualizado-queue-notification"
      topic_key = "livro_atualizado"
    }
    livro_atualizado_queue_catalog = {
      name      = "livro-atualizado-queue-catalog"
      topic_key = "livro_atualizado"
    }
    livro_alugado_queue_notification = {
      name      = "livro-alugado-queue-notification"
      topic_key = "livro_alugado"
    }
    livro_alugado_queue_catalog = {
      name      = "livro-alugado-queue-catalog"
      topic_key = "livro_alugado"
    }
    livro_retornado_queue_notification = {
      name      = "livro-retornado-queue-notification"
      topic_key = "livro_retornado"
    }
    livro_retornado_queue_catalog = {
      name      = "livro-retornado-queue-catalog"
      topic_key = "livro_retornado"
    }
    livro_lembrete_queue_notification = {
      name      = "livro-lembrete-queue-notification"
      topic_key = "livro_lembrete_notification"
    }
  }
}

resource "aws_sns_topic" "topics" {
  for_each = local.sns_topics
  name     = each.value
}

resource "aws_sqs_queue" "queues" {
  for_each = local.sqs_queues
  name     = each.value.name
}

resource "aws_sqs_queue_policy" "queue_policies" {
  for_each  = local.sqs_queues
  queue_url = aws_sqs_queue.queues[each.key].id
  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Effect    = "Allow"
      Principal = "*"
      Action    = "sqs:SendMessage"
      Resource  = aws_sqs_queue.queues[each.key].arn
      Condition = {
        ArnEquals = {
          "aws:SourceArn" = aws_sns_topic.topics[each.value.topic_key].arn
        }
      }
    }]
  })
}

resource "aws_sns_topic_subscription" "topic_subscriptions" {
  for_each  = local.sqs_queues
  topic_arn = aws_sns_topic.topics[each.value.topic_key].arn
  protocol  = "sqs"
  endpoint  = aws_sqs_queue.queues[each.key].arn
}
