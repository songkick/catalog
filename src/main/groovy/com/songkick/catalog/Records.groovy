package com.songkick.catalog

import com.android.ddmlib.logcat.LogCatMessage
import com.android.utils.SparseArray


class Records {
    private List<LogCatMessage> messages
    private SparseArray<Starter> starters;

    Records(List<LogCatMessage> messages, SparseArray<Starter> starters) {
        this.messages = messages
        this.starters = starters
    }

    List<LogCatMessage> getMessages() {
        return messages
    }

    SparseArray<Starter> getStarters() {
        return starters
    }
}
