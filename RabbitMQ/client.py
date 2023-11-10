

"""
Código python que permite ao usuário se inscrever em tópicos do RabbitMQ e receber mensagens deles. Para selecionar o tópico de interesse, 
é solicitado ao usuário que selecione os tópicos disponíveis. O script então se conecta ao RabbitMQ, cria um canal, declara uma exchange do tipo topic,
cria uma fila para o cliente, faz o bind da fila com a exchange usando os tópicos selecionados e começa a consumir as mensagens da fila.

Autores: Thiago Gariani Quinto e Marcos Vinicius de Quadros

Data de criação: 04/11/2023
Datas de atualização: 05/11/2023, 07/11/2023, 08/11/2023, 10/11/2023
"""
import pika
import questionary
import json

"""
Função que é chamada quando uma mensagem é recebida. Ela imprime o autor e o conteúdo da mensagem.
"""
def callback(ch, method, properties, body):
    content = json.loads(body.decode('utf-8'))
    print(content['name'] )
    print(content['text'] + "\n\n")

topics = ['basketball', 'football'] # tópicos disponíveis

# perguntando ao usuário qual tópico deseja assinar
subscribed = questionary.checkbox(
    "Em quais tópicos você deseja se inscrever?",
    choices=topics,
    instruction="Use as setas para navegar, <espaço> para selecionar, <a> para alternar, <i> para inverter"
).ask()

print("Tópicos nos quais você se inscreveu:", subscribed)

# conectando ao RabbitMQ
connection = pika.BlockingConnection(pika.ConnectionParameters('localhost'))
channel = connection.channel()

# criando um canal de comunicação
channel = connection.channel()

# declarando uma exchange do tipo topic
channel.exchange_declare(exchange='topic_logs', exchange_type='topic')

# criando uma fila para o cliente
result = channel.queue_declare(queue='', exclusive=True)
queue_name = result.method.queue 

# fazendo o bind da fila com a exchange usando o tópico desejado
for topic in subscribed:
    channel.queue_bind(exchange='topic_logs', queue=queue_name, routing_key=topic)

# consumir as mensagens da fila
channel.basic_consume(queue=queue_name, on_message_callback=callback, auto_ack=True)

channel.start_consuming()
