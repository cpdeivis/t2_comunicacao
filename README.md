# Trabalho 2 - Comunicação de Dados

Trabalho sobre o controle do enlace da dados usando sockets.

## Framing
Endereço Partida | Endereço Chegada | ACK  | CheckSum | Length  |  Dado
------------ | ------------- | ------------- | ------------- | ------------- | -------------
4 bytes | 4 bytes | 1 bytes | 2 bytes | 4 bytes | X bytes

## Controle do Fluxo de Dados
Go-Back-N ARQ.

## Controle do Erro
Checksum.

## Linguagem
Java.