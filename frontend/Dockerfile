# ===============================
# Build stage
# ===============================
FROM node:20-alpine AS build
WORKDIR /app

# Copy only package.json & package-lock.json for caching
COPY package*.json ./
RUN npm install

# Copy all source files and build
COPY . .
RUN npm run build

# ===============================
# Serve stage (Nginx)
# ===============================
FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html

# Expose HTTP port
EXPOSE 80
