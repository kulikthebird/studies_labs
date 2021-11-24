
DATABASE=database.txt
PRIVATE_KEY=private.pem
PUBLIC_KEY=cert.pem

TRUSTED=trustedThirdParty/

FIRST_SERVER=bankA
FIRST_SERVER_USER_PORT=5001
FIRST_SERVER_SERVICE_PORT=6001

SECOND_SERVER=bankB
SECOND_SERVER_USER_PORT=5002
SECOND_SERVER_SERVICE_PORT=6002


runServer1:
	python main.py --user-port $(FIRST_SERVER_USER_PORT) --service-port $(FIRST_SERVER_SERVICE_PORT) \
		--pub $(FIRST_SERVER)/$(PUBLIC_KEY) --priv $(FIRST_SERVER)/$(PRIVATE_KEY) \
		--db $(FIRST_SERVER)/$(DATABASE) --trusted $(TRUSTED)

runServer2:
	python main.py --user-port $(SECOND_SERVER_USER_PORT) --service-port $(SECOND_SERVER_SERVICE_PORT) \
		--pub $(SECOND_SERVER)/$(PUBLIC_KEY) --priv $(SECOND_SERVER)/$(PRIVATE_KEY) \
		--db $(SECOND_SERVER)/$(DATABASE) --trusted $(TRUSTED)

clean:
	rm *.pyc