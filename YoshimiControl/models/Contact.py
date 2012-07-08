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


from sqlalchemy import Column, ForeignKey
from sqlalchemy.types import Unicode, Integer
from models.BaseObject import BaseObject

class Contact(BaseObject):

    phone_bot_id = Column(Integer, ForeignKey('phone_bot.id'), nullable = False)
    name = Column(Unicode(64))
    phone_number = Column(Unicode(64))
    email = Column(Unicode(64))

    @classmethod
    def get_all(cls):
        """ Return all Contact objects """
        return dbsession.query(cls).all()

    @classmethod
    def by_id(cls, contact_id):
        """ Return the Contact object whose id is 'contact_id' """
        return dbsession.query(cls).filter_by(id = contact_id.encode('utf-8', 'ignore')).first()

