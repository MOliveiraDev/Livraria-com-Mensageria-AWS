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