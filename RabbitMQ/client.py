
import pika
import questionary

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
