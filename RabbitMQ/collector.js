/**
 * 
 * @description lê o o arquivo csv dos tweets e envia para o RabbitMQ caso contenha alguma palavra chave 
 * relacionada ao futebol ou basquete
 * 
 * @author Thiago Gariani Quinto
 * @author Marcos Vinicius de Quadros
 * @since 04/11/2023
 * @updates 05/11/2023, 07/11/2023
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
 * envia a mensagem para o RabbitMQ  
 * @param {dados do csv file} data 
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

            const exchange = 'tweets_exchange'; // nome da exchange
            const queue = 'tweets_queue'; // nome da fila

            channel.assertExchange(exchange, 'direct', { durable: false });
            channel.assertQueue(queue, { durable: false });
            channel.bindQueue(queue, exchange, 'tweets'); // bind da fila com a exchange - chave 'tweets'

            const formated_data = {
                name: data.name,
                text: data.text,
            }

            formated_data.topic = topic;

            // channel.publish(exchange, 'tweets', Buffer.from(JSON.stringify(formated_data)));
            channel.sendToQueue(queue, Buffer.from(JSON.stringify(formated_data)));

            setTimeout(() => {
                connection.close();
            }, 500);
        });
    });
}

/**
 * lê o csv file e envia para o RabbitMQ caso contenha alguma palavra chave
 */
function readCsvFile() {
    fs.createReadStream('./tweets_data.csv') // caminho do arquivo csv
        .pipe(csv())
        .on('data', (data) => {
            const { name, text } = data; // pega o nome e o texto do tweet
            const containsFootball = sub_words_football.some(word => {
                const regex = new RegExp(`\\b${word}\\b`, 'i'); // regex para verificar se a palavra está contida no texto - ignorando case sensitive
                return regex.test(name) || regex.test(text); 
            });
            const containsBasketball = sub_words_basketball.some(word => {
                const regex = new RegExp(`\\b${word}\\b`, 'i'); // regex para verificar se a palavra está contida no texto - ignorando case sensitive
                return regex.test(name) || regex.test(text); 
            });
            if (containsFootball || containsBasketball) {
                let topic = containsFootball ? 'football' : 'basketball'; // define o tópico do tweet
                sendToRabbitMQ(data, topic);
            }
        })
        .on('end', () => {
            console.log('Arquivo csv lido com sucesso');
        });
}

function main() {
    readCsvFile();
}

main();
