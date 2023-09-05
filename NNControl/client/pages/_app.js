import { getUser } from '@/components/api'
import '@/styles/globals.css'
import Head from 'next/head'
import { useRouter } from 'next/router'
import { useState, useEffect } from 'react'
import { AnimatePresence } from 'framer-motion'

export default function App({ Component, pageProps }) {

  const router = useRouter()

  const [username, setUsername] = useState("")
  const [admin, setAdmin] = useState(false)
  const [status, setStatus] = useState(200)
  const [statusText, setStatusText] = useState("")

  useEffect(() => {
    const interval = setInterval(() => {
      if (status != 200) {
        getUser(router, setStatus, setStatusText, setUsername, setAdmin)
      }
    }, 8000);
    return () => clearInterval(interval);
  }, [status])

  return (
    <>
      <Head>
        <title>Nikocraft Console</title>
        <meta name='description' content='The Nikocraft Network Engine Console' />
      </Head>
      <AnimatePresence mode="wait" initial={false}>
        <Component
          username={username} setUsername={setUsername}
          admin={admin} setAdmin={setAdmin}
          status={status} setStatus={setStatus}
          statusText={statusText} setStatusText={setStatusText}
          {...pageProps} key={router.asPath}/>
      </AnimatePresence>
    </>
  )

}
