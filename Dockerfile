FROM eclipse-temurin:21-jdk

RUN apt-get update && apt-get install -y \
    tesseract-ocr \
    tesseract-ocr-eng \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY . .

RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

EXPOSE 8080

ENV TESSDATA_PREFIX=/usr/share/tesseract-ocr/5/tessdata/

CMD ["sh", "-c", "java -jar target/*.jar"]