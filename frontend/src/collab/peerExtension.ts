import { Update, collab, getSyncedVersion, receiveUpdates, sendableUpdates } from "@codemirror/collab"
import { EditorView, ViewPlugin, ViewUpdate } from "@codemirror/view"
import { pullUpdates, pushUpdates, syncCursors } from "./collabFunctions"
import { setRemoteCursors } from "./cursorField"
import { ChangeSet } from "@codemirror/state"

// nick 3a427d49-0e7c-46d4-95c0-18ca6b34aa48
const USER_ID_BOT = ""

export function peerExtension(projectId: string, startVersion: number, userId : string) {
    let plugin = ViewPlugin.fromClass(class {
      private pushing = false
      private done = false
      private clientId = ""
      private botInterval: NodeJS.Timeout | null = null
  
      constructor(private view: EditorView) { 
        if(userId == USER_ID_BOT)  this.simulateTyping()
        this.pull()
        this.syncCursorsLoop()
      }
  
      update(update: ViewUpdate) {
        if (update.docChanged) this.push()
      }
  
      async push() {
        let updates = sendableUpdates(this.view.state)
        if (this.pushing || !updates.length) return
        this.clientId = updates[0].clientID
        this.pushing = true
        let version = getSyncedVersion(this.view.state)
        
        await pushUpdates(projectId, version, updates, this.view.state.selection.main)
        this.pushing = false
        // Regardless of whether the push failed or new updates came in
        // while it was running, try again if there's updates remaining
        let newUpdates = sendableUpdates(this.view.state)
        if (newUpdates.length){
          setTimeout(() => this.push(), 100)
        }
      }
  
      async pull() {
        while (!this.done) {
          let version = getSyncedVersion(this.view.state)
          let updates = await pullUpdates(projectId, version)
          let upd: Update[] = updates.map((u: any) => ({
            clientID: u.update.clientID,
            changes: u.update.changes,
          }))
          if(updates.length > 0){
            //this.view.dispatch(receiveUpdates(this.view.state, upd))
            const effects = [];
            const cursorMap = new Map();
            for (let update of updates) {
              if (update.cursor && update.update.clientID !== this.clientId) {
                cursorMap.set(update.update.clientID, update.cursor);
              }
            }
            if (cursorMap.size > 0) {
              effects.push(setRemoteCursors.of(cursorMap));
            }
            this.view.dispatch({...receiveUpdates(this.view.state, upd), effects})
          }
        }
      }

      async syncCursorsLoop() {
        while (!this.done) {
          try {
            const selection = this.view.state.selection.main
            const localCursor = { from: selection.from, to: selection.to }
            const others = await syncCursors(projectId, userId, localCursor)
  
            // Update the shared map of remote cursors
            /*remoteCursors.clear()
            for (let { userId: uid, cursor } of others) {
              if (uid !== userId) remoteCursors.set(uid, cursor)
            }*/

            this.view.dispatch({
              effects: setRemoteCursors.of(new Map(
                others
                  .filter(({ userId: uid }) => uid !== userId)
                  .map(({ userId: uid, cursor }) => [uid, cursor])
              ))
            })
  
          } catch (err) {
            console.error("Failed cursor sync", err)
          }
  
          await new Promise(r => setTimeout(r, 3000)) 
        }
      }
  
      simulateTyping() {
        this.botInterval = setInterval(() => {
          const pos = 0
          const randomChar = "\n\n\n\n\n\n\n\n\n\n\n\n" 
          const tr = this.view.state.update({
            changes: { from: pos, to: pos, insert: randomChar }
          })
          this.view.dispatch(tr)
        }, 500)
      }

      destroy() { this.done = true; if (userId == "3a427d49-0e7c-46d4-95c0-18ca6b34aa48") clearInterval(this.botInterval)}
    })
    return [collab({startVersion}), plugin]
  }