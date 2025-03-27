import * as React from 'react';
import { createRoot } from 'react-dom/client'

export function app() {
    createRoot(document.getElementById("root")).render(
        <div>
            <h1> HELLO!</h1>
        </div>
    )
}

