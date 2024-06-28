IMAGE_NAME=survey-backend

build:
	docker build -t $(IMAGE_NAME) .

start: 
	docker run -p 3004:3004 $(IMAGE_NAME)