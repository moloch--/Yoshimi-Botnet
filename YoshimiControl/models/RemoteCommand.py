# -*- coding: utf-8 -*-
'''
Created on Mar 12, 2012

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


from models import dbsession
from models.BaseObject import BaseObject
from sqlalchemy import Column
from sqlalchemy.types import Unicode, Boolean, Integer
from sqlalchemy.orm import relationship, backref

class RemoteCommand(BaseObject):

    phone_bot_id = Column(Integer, ForeignKey('phone_bot.id'), nullable = False)
    command = Column(Unicode(64), nullable = False)
    completed = Column(Boolean, default = False, nullable = False)

    @classmethod
    def by_id(cls, command_id):
        """ Return the RemoteCommand object whose id is 'command_id' """
        return dbsession.query(cls).filter_by(id = command_id).first()

    @classmethod
    def qsize(cls):
        ''' Returns the number of incompelte commands left in the database '''
        return dbsession.query(cls).filter_by(completed = False).count()

    @classmethod
    def pop(cls):
        ''' Pop a command off the "queue" or return None if not jobs remain '''
        return dbsession.query(cls).filter_by(completed = False).order_by(cls.created).first()