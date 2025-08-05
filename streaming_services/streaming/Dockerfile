FROM node:18.17.1

WORKDIR /usr/src/app

COPY package*.json ./

RUN npm ci --omit=dev

COPY ./src ./src
COPY ./public ./public
# Other static assets go here as well if needed

EXPOSE 80

CMD [ "node", "./src/index.js" ]