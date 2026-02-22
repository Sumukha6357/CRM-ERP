FROM node:20-alpine AS deps
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm ci

FROM node:20-alpine AS build
WORKDIR /app
COPY --from=deps /app/node_modules ./node_modules
COPY . .
ARG NEXT_PUBLIC_API_BASE_URL
ARG NEXT_PUBLIC_APP_NAME
ARG NEXT_PUBLIC_ENV
ARG NEXT_PUBLIC_DEBUG
ENV NEXT_PUBLIC_API_BASE_URL=$NEXT_PUBLIC_API_BASE_URL
ENV NEXT_PUBLIC_APP_NAME=$NEXT_PUBLIC_APP_NAME
ENV NEXT_PUBLIC_ENV=$NEXT_PUBLIC_ENV
ENV NEXT_PUBLIC_DEBUG=$NEXT_PUBLIC_DEBUG
ENV NODE_ENV=production
RUN npm run build

FROM node:20-alpine AS runner
WORKDIR /app
ENV NODE_ENV=production
COPY --from=build /app/.next ./.next
COPY --from=build /app/public ./public
COPY --from=build /app/package.json ./package.json
COPY --from=build /app/node_modules ./node_modules
EXPOSE 3000
CMD ["npm", "run", "start"]
