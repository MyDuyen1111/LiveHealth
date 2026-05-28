FROM python:3.10-slim

WORKDIR /app

# Copy requirements and install dependencies
COPY requirements.txt ./
RUN pip install --no-cache-dir -r requirements.txt

# Copy code files
COPY . .

# Expose port (matches default port 62000)
EXPOSE 62000

# Start FastAPI app
CMD ["python", "main.py"]
