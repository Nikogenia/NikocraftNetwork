import { MdSend } from "react-icons/md"

export default function ControlBar({commandInput, setCommandInput, submit, online, mode, changeMode}) {

    const handleKeyPress = (e) => {
        if (e.key == "Enter") {
            submit()
        }
    }

    return (
        <div className="flex items-center justify-center bg-indigo-900 w-5/6 h-11 mt-4 rounded-xl">
            {
                (online) ? (
                    <>
                    <div className="bg-indigo-300 text-indigo-950 text-xl font-mono font-extrabold px-2 ml-2 mr-2
                        rounded border-indigo-200 border-2">/</div>
                    <input className="bg-indigo-300 text-indigo-950 rounded text-xl font-mono px-1
                        hover:bg-indigo-400 transition-colors w-full placeholder-indigo-400
                        duration-150 border-indigo-200 hover:border-indigo-100 border-2"
                        type="text" value={commandInput} autoFocus
                        placeholder="help"
                        onChange={(e) => setCommandInput(e.target.value)}
                        onKeyDown={handleKeyPress}></input>
                    <button className="bg-indigo-500 rounded px-1 ml-2  
                        text-indigo-50 text-3xl hover:bg-indigo-600 transition-colors
                        duration-150 border-indigo-400 hover:border-indigo-300 border-2"
                        onClick={submit}><MdSend /></button>
                    <div className="bg-lime-700 text-lime-200 text-xl font-mono font-extrabold px-2 ml-2 mr-2
                        rounded border-lime-600 border-2">ONLINE</div>
                    </>
                ) : (
                    <div className="bg-red-700 text-red-200 text-xl font-mono font-extrabold w-full ml-2 mr-2
                    rounded border-red-600 border-2 text-center">OFFLINE</div>
                )
            }
            <select value={mode} onChange={(e) => changeMode(e.target.value)} className="text-xl rounded mr-2 bg-purple-800 text-purple-200
                font-bold border-2 border-purple-700 text-center hover:border-purple-600 hover:bg-purple-900">
                <option value="off" className="text-xl rounded mr-2 bg-purple-800 text-purple-200
                    font-bold border-2 border-purple-700 text-center hover:border-purple-600 hover:bg-purple-900">Off</option>
                <option value="manuel" className="text-xl rounded mr-2 bg-purple-800 text-purple-200
                    font-bold border-2 border-purple-700 text-center hover:border-purple-600 hover:bg-purple-900">Manuel</option>
                <option value="failure" className="text-xl rounded mr-2 bg-purple-800 text-purple-200
                    font-bold border-2 border-purple-700 text-center hover:border-purple-600 hover:bg-purple-900">Restart Failure</option>
                <option value="always" className="text-xl rounded mr-2 bg-purple-800 text-purple-200
                    font-bold border-2 border-purple-700 text-center hover:border-purple-600 hover:bg-purple-900">Restart Always</option>
            </select>
        </div>
    )

}
