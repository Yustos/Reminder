from tornado.web import RequestHandler
from Logger import LogApi
import Storage
import json
import time

class SyncServiceHandler(RequestHandler):
    def __init__(self, application, request, **kwargs):
        self._storage = Storage.Storage()
        return super(SyncServiceHandler, self).__init__(application, request, **kwargs)

    @LogApi()
    def get(self, path):
        date = self.get_argument("date", default=None, strip=False)
        result = self._storage.getFromDate(float(date))
        self.write({"items": [r.dict() for r in result]})

    @LogApi()
    def post(self, path):
        input = json.loads(self.request.body)
        remoteRecord = Storage.Record(input["id"], time.time(), input["title"], input["body"], input["state"])
        localRecord = self._storage.getDetail(remoteRecord.id)
        result = self.syncRecord(remoteRecord, localRecord)
        if result is not None:
            self.write(result.dict())

    def syncRecord(self, remote, local):
        if remote.state == 4:
            return self._storage.delete(remote.id)
        else:
            return self._storage.update(remote.id, remote.title, remote.body)