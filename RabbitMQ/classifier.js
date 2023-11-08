/**
 * 
 * @description lê o o arquivo csv dos tweets e envia para o RabbitMQ caso contenha alguma palavra chave 
 * relacionada ao futebol ou basquete
 * 
 * @author Thiago Gariani Quinto
 * @author Marcos Vinicius de Quadros
 * @since 04/11/2023
 * @updates 05/11/2023, 07/11/2023, 08/11/2023
 */

const fs = require('fs');
const csv = require('csv-parser');
const amqp = require('amqplib/callback_api');

// palavras relacionadas a futebol 
var sub_words_football = ['soccer', 'football', 'premier league', 'la liga', 'bundesliga', 'serie a', 'ligue one',
    'cristiano ronaldo', 'messi', 'pele', 'neymar', 'mbappe', 'ronaldinho', 'zidane', 'maradona',
    'real madrid', 'barcelona', 'manchester united', 'manchester city', 'liverpool', 'chelsea',
    'bayern munich', 'borussia', 'juventus', 'inter milan', 'ac milan', 'psg', 'arsenal', 'tottenham',
    'champions league', 'europa league', 'world cup', 'euro cup', 'copa del rey', 'fifa', 'uefa']

// palavras relacionadas a basquete
var sub_words_basketball = ['nba', 'lebron', 'kevin durant', 'kobe bryant', 'michael jordan', 'stephen curry', 'giannis', 'warriors',
    'lakers', 'clippers', 'celtics', 'bulls', 'rockets', 'spurs', 'knicks', 'nets', 'bucks', 'mavericks',
    'playoffs', 'play-in', 'nba finals', 'basketball']

/**
 * consome os dados de tweets do RabbitMQ que foram enviados pelo coletor
 */
function consumeFromRabbitMQ() {
    amqp.connect('amqp://localhost', (error0, connection) => {
        if (error0) {
            console.log("Erro ao conectar com o RabbitMQ", error0);
            return;
        }
        connection.createChannel((error1, channel) => {
            if (error1) {
                console.log("Erro ao criar o canal", error1);
                return;
            }

            const exchange = 'tweets_exchange'; // nome da exchange
            const queue = 'tweets_queue'; // nome da fila

            channel.assertExchange(exchange, 'direct', { durable: false });
            channel.assertQueue(queue, { durable: false });
            channel.bindQueue(queue, exchange, 'tweets'); // bind da fila com a exchange - chave 'tweets'

            channel.consume(queue, (message) => {
                const data = JSON.parse(message.content.toString());
                hasTopic(data); // verifica se o tweet contém alguma palavra chave relacionada ao futebol ou basquete
            }, {
                noAck: true
            });
        });
    });
}

/**
 * função que verifica se o tweet em questão contém alguma palavra chave relacionada ao futebol ou basquete,
 * se sim, envia para o RabbitMQ informando qual o tópico do tweet
 * @param {dado do tweet} content 
 */
function hasTopic(content) {
    const {name, text} = content;
    const containsFootball = sub_words_football.some(word => {
        const regex = new RegExp(`\\b${word}\\b`, 'i'); // verifica se a palavra está contida no texto - ignorando case sensitive
        return regex.test(name) || regex.test(text);
    });
    const containsBasketball = sub_words_basketball.some(word => {
        const regex = new RegExp(`\\b${word}\\b`, 'i'); // verifica se a palavra está contida no texto - ignorando case sensitive
        return regex.test(name) || regex.test(text);
    });

    if (containsFootball || containsBasketball) {
        let topic = containsFootball ? 'football' : 'basketball'; // define o tópico do tweet
        sendToRabbitMQ(content, topic);
    }
}

/**
 * envia a mensagem para a fila do RabbitMQ relacinada ao tópico do tweet, senda consumida pelos clientes posteriormente
 * @param {dadodo tweet} data 
 * @param {indica qual o tópico do data} topic 
 */
function sendToRabbitMQ(data, topic) {

    amqp.connect('amqp://localhost', (error0, connection) => {
        if (error0) {
            console.log("Erro ao conectar com o RabbitMQ", error0);
            return;
        }
        connection.createChannel((error1, channel) => {
            if (error1) {
                console.log("Erro ao criar o canal", error1);
                return;
            }

            const exchange = 'topic_logs'; // nome da exchange
            let queue = ''; // nome da fila

            topic == 'football' ? queue = 'football_queue' : queue = 'basketball_queue';

            channel.assertExchange(exchange, 'topic', { durable: false });

            // selecona apenas os dados importantes do tweet
            const formated_data = {
                name: data.name,
                text: data.text,
            }

            channel.publish(exchange, topic, Buffer.from(JSON.stringify(formated_data))); // publica a mensagem na fila do RabbitMQ
 
            setTimeout(() => {
                connection.close();
            }, 500);
        });
    });
}

function main() {
    consumeFromRabbitMQ();
}

main();
