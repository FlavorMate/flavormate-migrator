FROM eclipse-temurin:21-jre-alpine

RUN apk add --no-cache \
    # Used for health check
    curl \
    # Core image processing suite
    imagemagick  \
    # JPEG image format
    libjpeg-turbo  \
    # PNG image format
    libpng  \
    # TIFF image format
    tiff  \
    # WebP image format
    libwebp  \
    # HEIF/HEIC image formats
    libheif  \
    # GIF image format
    giflib  \
    # RAW image formats
    libraw  \
    # JPEG 2000 image format
    openjpeg  \
    # EXIF metadata in image files
    libexif


RUN addgroup -g 185 -S app &&  \
    adduser -u 185 -S app -G app &&  \
    mkdir -p /app &&  \
    chown -R app:app /app

USER app

WORKDIR /app

# Copy only necessary Quarkus app parts
COPY build/flavormate-migrator.jar /app/

# JVM tuning
ENV JAVA_OPTS="\
  -XX:+UseContainerSupport \
  -XX:MaxRAMPercentage=80 \
"

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar flavormate-migrator.jar"]
