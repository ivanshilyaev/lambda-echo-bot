## Lambda echo bot

Implementation steps:

1. Create Telegram bot using BotFather

2. Create API Gateway

3. Create Lambda

4. Connect Lambda to API Gateway and deploy API

5. Set a webhook:

`https://api.telegram.org/bot<your-bot-token>/setWebHook?url=<your-API-invoke-URL>`

6. Update Lambda code

7. Add bot token to Lambda environment variables

---

by [@ivanshilyaev](https://github.com/ivanshilyaev), 2021