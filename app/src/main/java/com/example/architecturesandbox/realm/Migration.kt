package com.example.architecturesandbox.realm

import io.realm.DynamicRealm
import io.realm.RealmMigration

class Migration: RealmMigration {

    private val TAG = "Migration"

    companion object {
        const val SCHEMA_VERSION: Long = 0
    }

    override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {}

}