from tornado.ioloop import IOLoop
from tornado.web import RequestHandler, Application, url
from tornado import log
import logging
import Storage
import os
import json
import time

class ListServiceHandler(RequestHandler):
    def __init__(self, application, request, **kwargs):
        self._storage = Storage.Storage()
        return super(ListServiceHandler, self).__init__(application, request, **kwargs)

    def get(self):
        self.write(self._getItems())

    def delete(self):
        self._storage.delete(self.get_query_argument("name"))
        self.write(self._getItems())

    def _getItems(self):
        items = self._storage.get()
        return {"items": [{'id': i.id, 'date' : i.date,'title':i.title} for i in items]}

class DetailServiceHandler(RequestHandler):
    def __init__(self, application, request, **kwargs):
        self._storage = Storage.Storage()
        return super(DetailServiceHandler, self).__init__(application, request, **kwargs)

    def get(self, id):
        item = self._storage.getDetail(id)
        self.write({'id': item.id, 'date' : item.date,'title':item.title, 'body': item.body})

    def post(self,id):
        input = json.loads(self.request.body)
        if 'id' in input:
            newItem = self._storage.update(input["id"], input["title"], input["body"])
        else:
            newItem = self._storage.add(input["title"], input["body"])
        self.write({'id': newItem.id, 'date': newItem.date, 'title': newItem.title, 'body': newItem.body})

    def delete(self, id):
        self._storage.delete(id)

application = Application([
    url(r"/api/list", ListServiceHandler, name="listService"),
    url(r"/api/detail/(.*)", DetailServiceHandler, name="detailService"),
])

if __name__ == "__main__":
    application.listen(9359)
    IOLoop.instance().start()

