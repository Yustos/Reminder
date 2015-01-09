from tornado.web import RequestHandler
import Storage
import json
from Logger import Logger


class DetailServiceHandler(RequestHandler):
    def __init__(self, application, request, **kwargs):
        self._storage = Storage.Storage()
        return super(DetailServiceHandler, self).__init__(application, request, **kwargs)

    def get(self, path):
        id = self.get_argument("id", default=None, strip=False)
        item = self._storage.getDetail(id)
        self.write(item.dict())

    def post(self,id):
        input = json.loads(self.request.body)
        #TODO: log
        Logger.InfoApi(input)
        if 'id' in input:
            item = self._storage.update(input["id"], input["title"], input["body"])
        else:
            item = self._storage.add(input["title"], input["body"])
        #TODO: log
        Logger.InfoApi(item.dict())
        self.write(item.dict())

    def delete(self, path):
        id = self.get_argument("id", default=None, strip=False)
        self._storage.delete(id)