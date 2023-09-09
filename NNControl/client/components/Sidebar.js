import { useEffect, useState } from "react"
import { MdChevronRight, MdExpandMore } from "react-icons/md"

export default function Sidebar({servers}) {

    const [agents, setAgents] = useState([])
    const [updater, setUpdater] = useState(0)

    const render = () => {
        setUpdater(updater + 1)
    }

    useEffect(() => {
        let new_agents = []
        servers.forEach(server => {
            for (const agent of new_agents) {
                if (agent.name == server.agent) return
            }
            new_agents.push({
                name: server.agent,
                showChildren: true
            })
        })
        setAgents(new_agents)
    }, [servers])

    return (
        <div className="flex flex-col bg-indigo-900 w-60 overflow-x-hidden overflow-y-auto">
            <div className="text-2xl bg-indigo-800 text-indigo-50 pb-3 pt-2 text-center">Server</div>
            {
                agents.map(value => <Agent key={value.name} agent={value} servers={servers} render={render}/>)
            }
        </div>
    )

}


function Agent({agent, servers, render}) {

    const getChildren = () => {
        const children = []
        servers.forEach(server => {
            if (server.agent == agent.name) children.push(server)
        })
        return children
    }
    
    return (
        <>
        <button className="flex items-center bg-indigo-600 text-indigo-50 pl-1"
            onClick={() => {
                agent.showChildren = !agent.showChildren
                render()
            }}>
            {(agent.showChildren) ? (
                <MdExpandMore className="text-3xl" />) : (
                <MdChevronRight className="text-3xl" />)}
            <div className="text-xl pb-2 pt-1">{agent.name}</div>
        </button>
        {(agent.showChildren) ? (
            getChildren().map(value => <Server key={value.name} server={value}/>)) : <></>
        }
        </>
    )

}


function Server({server}) {
    
    return (
        <button className="flex items-center justify-between bg-indigo-500 text-indigo-50 pl-1">
            <div className="text-xl pb-2 pt-1 pl-1">{server.name}</div>
            <div className="bg-red-500 w-3 h-3 rounded-full border mr-2 border-indigo-800"></div>
        </button>
    )

}
