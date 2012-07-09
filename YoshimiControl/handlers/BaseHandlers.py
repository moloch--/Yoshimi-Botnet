# -*- coding: utf-8 -*-
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


import logging

from models import User, PhoneBot
from libs.SecurityDecorators import *
from libs.Session import SessionManager
from tornado.web import RequestHandler

class UserBaseHandler(RequestHandler):
    ''' User handlers extend this class '''
    
    def initialize(self, dbsession):
        self.dbsession = dbsession
        self.session_manager = SessionManager.Instance()
        self.session = self.session_manager.get_session(self.get_secure_cookie('auth'), self.request.remote_ip)
    
    def get_current_user(self):
        if self.session != None:
            return User.by_user_name(self.session.data['user_name'])
        return None


class BotBaseHandler(RequestHandler):
    ''' Bot handlers extend this class '''
    
    def initialize(self, dbsession):
        self.dbsession = dbsession
        self.bot = self.get_current_user()
    
    def get_current_user(self):
        return PhoneBot.by_uuid(self.request.headers['Uuid'].encode('utf-8', 'ignore'))