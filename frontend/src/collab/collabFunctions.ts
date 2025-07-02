import { Update } from "@codemirror/collab"
import { getCursors, getInitialDoc, getUpdates, sendUpdates } from "../http/collabApi"
import { ChangeSet, SelectionRange, Text } from "@codemirror/state"
import { CursorInfo } from "../domain/CursorInfo"


export function pushUpdates(projectId: string, version: number, fullUpdates: readonly Update[], selection: SelectionRange) {
    const updates = fullUpdates.map(u => ({
        clientID: u.clientID,
        changes: u.changes.toJSON(),
        cursor: {from: selection.from, to: selection.to}
    }))
    return sendUpdates(projectId, version, {updates})
}
  
export function pullUpdates(projectId: string, version: number) {
    return getUpdates(projectId, version).then( updates => 
        updates.map((u: any) => ({
            update: {
                clientID: u.clientID,
                changes: ChangeSet.fromJSON(u.changes),
            },
            cursor: {from: u.cursor.from, to: u.cursor.to}
        }))
    )
}
  
export function getDocument(projectId: string,) {
    return getInitialDoc(projectId).then((data: any) => ({
        version: data.version,
        doc: Text.of(data.doc.split("\n"))
    }))
}
  
  
export function syncCursors(projectId: string, userId: string, cursorInfo: CursorInfo) {
    return getCursors(projectId, userId, cursorInfo)
}


