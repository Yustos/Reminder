import time
import os
from enum import Enum
from CodernityDB.database import Database
from CodernityDB.tree_index import TreeBasedIndex
from CodernityDB.hash_index import HashIndex
from CodernityDB.database import RecordNotFound


class StateType(Enum):
    Server = -1
    Unknown = 0
    Normal = 1
    Modified = 2
    Added = 3
    Deleted = 4


class Record():
    def __init__(self, id, date, title, body, state=StateType.Unknown):
        self.id = id
        self.date = date
        self.title = title
        self.body = body
        self.state = state

    def dict(self):
        result = {
            "date": self.date,
            "title": self.title,
            "body": self.body,
            "state": self.state}
        if self.id is not None:
            result["id"] = self.id
        return result;


class Storage(object):
    """Database storage"""
    path = None

    def __init__(self):
        if Storage.path is None:
            raise Exception("Path for data not set")

        db = Database(os.path.join(Storage.path, "db"))
        if db.exists():
            db.open()
        else:
            db.create()
            db.add_index(ActiveIndex(db.path, "activeIndex"))
            db.add_index(DateTreeIndex(db.path, "dateIndex"))
        self._db = db

    def get(self, showDeleted):
        if showDeleted:
            return [self._map(r) for r in self._db.all("id")]
        else:
            return [self._map(r["doc"]) for r in self._db.get_many("activeIndex", True, with_doc=True)]

    def getDetail(self, id):
        try:
            record = self._db.get("id", id)
            return self._map(record)
        except RecordNotFound:
            return None

    def update(self, id, title, body):
        record = Record(id, time.time(), title.encode('utf-8'), body.encode('utf-8'), StateType.Modified)
        d = record.dict()
        try:
            exists = self._db.get("id", id)
            for key, value in d.iteritems():
                if key != "id":
                    exists[key] = value
            self._db.update(exists)
        except RecordNotFound:
            d["_id"] = id
            self._db.insert(d)
            record["id"] = id
        return record

    def add(self, title, body):
        record = Record(None, time.time(), title.encode('utf-8'), body.encode('utf-8'), StateType.Added)
        result = self._db.insert(record.dict())
        record.id = result["_id"]
        return record

    def delete(self, id):
        result = self._db.get("id", id)
        result["state"] = StateType.Deleted
        self._db.update(result)
        # self._db.delete(result)

    def getFromDate(self, date):
        records = self._db.get_many("dateIndex", limit=-1, start=date, end=None, inclusive_start=True, with_doc=True)
        return [self._map(r["doc"]) for r in records]

    def _map(self, r):
        return Record(r["_id"], r["date"], r["title"], r["body"], r["state"])


class DateTreeIndex(TreeBasedIndex):
    def __init__(self, *args, **kwargs):
        kwargs['node_capacity'] = 7
        kwargs['key_format'] = 'd'
        super(DateTreeIndex, self).__init__(*args, **kwargs)

    def make_key_value(self, data):
        a_val = data.get("date")
        if a_val is not None:
            return a_val, None
        return None

    def make_key(self, key):
        return key


class ActiveIndex(HashIndex):
    def __init__(self, *args, **kwargs):
        kwargs['key_format'] = '?'
        kwargs['hash_lim'] = 2
        super(ActiveIndex, self).__init__(*args, **kwargs)

    def make_key_value(self, data):
        val = data.get('state')
        if val is None:
            return None
        # TODO: StateType.Deleted
        return val != 4, None

    def make_key(self, key):
        return key