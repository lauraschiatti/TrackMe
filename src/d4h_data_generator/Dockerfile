FROM php:7-cli-alpine

WORKDIR /code
ADD . /code/

# installing curl
RUN apk add --update curl && rm -rf /var/cache/apk/*

# installing wait-for-it script
RUN curl https://raw.githubusercontent.com/eficode/wait-for/master/wait-for --output wait-for-it.sh
RUN chmod +x wait-for-it.sh

# RUN ["php", "./d4h_generator.php"]
