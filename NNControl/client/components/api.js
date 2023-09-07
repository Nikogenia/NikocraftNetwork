import { BACKEND_URL } from "./constant"

export async function apiRequest(path, body, setStatus, setStatusText) {

    try {

        console.info("API request to " + BACKEND_URL + path)

        const response = await fetch(BACKEND_URL + path, {
            method: 'POST',
            body: JSON.stringify(body),
            headers: {
                "Content-Type": 'application/json'
            },
            credentials: "include"
        })

        if (response.status != 200) {
            setStatus(response.status)
            setStatusText(response.statusText)
            return null
        }
        
        const data = await response.json()
        setStatus(200)
        setStatusText("")

        return data

    } catch (e) {

        console.error("API request failed: " + e)
        setStatus(0)
        setStatusText("")
        return null

    }

}


export async function getUser(router, setStatus, setStatusText, setUsername, setAdmin) {

    console.info("Get user")

    const data = await apiRequest("/user", {}, setStatus, setStatusText)
    if (data == null) return

    if (data.error != "success") {
        router.push("/user/login")
        return
    }

    console.info("User '" + data.username + "' (admin: " + data.admin + ")")
    
    setUsername(data.username)
    setAdmin(data.admin)

}
