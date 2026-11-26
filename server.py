import asyncio, json, os
import websockets
from websockets.asyncio.server import serve

PHONE_CLIENTS = set()
BROWSER_CLIENTS = set()

async def handler(ws):
    role = "unknown"
    try:
        first = await ws.recv()
        msg = json.loads(first)
        role = msg.get("role", "unknown")

        if role == "phone":
            PHONE_CLIENTS.add(ws)
            print(f"[+] phone connected ({len(PHONE_CLIENTS)})")
            async for frame in ws:
                if BROWSER_CLIENTS:
                    await asyncio.gather(*[b.send(frame) for b in BROWSER_CLIENTS], return_exceptions=True)
        elif role == "browser":
            BROWSER_CLIENTS.add(ws)
            print(f"[+] browser connected ({len(BROWSER_CLIENTS)})")
            async for _ in ws:
                pass
    except websockets.ConnectionClosed:
        pass
    finally:
        PHONE_CLIENTS.discard(ws)
        BROWSER_CLIENTS.discard(ws)
        print(f"[-] {role} disconnected")

async def main():
    # We move WebSocket to port 8081 so it doesn't conflict with the web server
    port = 8081
    async with serve(handler, "0.0.0.0", port, max_size=10_000_000):
        print(f"WebSocket server running on ws://0.0.0.0:{port}")
        await asyncio.Future()

if __name__ == "__main__":
    asyncio.run(main())