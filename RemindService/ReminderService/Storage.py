import datetime
import time
from CodernityDB.database import Database
from CodernityDB.hash_index import HashIndex

class Record():
    def __init__(self, id, date, title, body):
        self.id = id
        self.date = date
        self.title = title
        self.body = body

class Storage(object):
    """Database storage"""

    def __init__(self):
        db = Database('db')
        if db.exists():
            db.open()
        else:
            db.create()
        self._db = db

    def get(self):
        records = self._db.all("id")
        return [self._map(r) for r in records]

    def getDetail(self, id):
        record = self._db.get("id", id)
        return self._map(record)

    def update(self, id, title, body):
        result = self._db.get("id", id)
        result["title"] = title.encode('utf-8')
        result["date"] = time.time()
        result["body"] = body.encode('utf-8')
        self._db.update(result)
        return self._map(result)

    def add(self, title, body):
        newRecord = self._db.insert(dict(date = time.time(), title = title.encode('utf-8'), body = body.encode('utf-8')))
        result = self._db.get("id", newRecord["_id"])
        return self._map(result)

    def delete(self, id):
        result = self._db.get("id", id)
        self._db.delete(result)

    def _map(self, r):
        return Record(r["_id"], r["date"], r["title"], r["body"])
