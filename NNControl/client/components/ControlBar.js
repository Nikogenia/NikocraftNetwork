import { MdSend } from "react-icons/md"

export default function ControlBar({commandInput, setCommandInput, submit, online}) {

    const handleKeyPress = (e) => {
        if (e.key == "Enter") {
            submit()
        }
    }

    return (
        <div className="flex items-center justify-center bg-indigo-900 w-5/6 h-11 mt-4 rounded-xl">
            <div className="bg-indigo-300 text-indigo-950 text-xl font-mono font-extrabold px-2 ml-2 mr-2
                rounded border-indigo-200 border-2">/</div>
            <input className="bg-indigo-300 text-indigo-950 rounded text-xl font-mono px-1
                hover:bg-indigo-400 transition-color w-full
                duration-300 border-indigo-200 hover:border-indigo-100 border-2"
                type="text" value={commandInput}
                onChange={(e) => setCommandInput(e.target.value)}
                onKeyDown={handleKeyPress}></input>
            <button className="bg-indigo-500 rounded px-1 ml-2 mr-9
                text-indigo-50 text-3xl hover:bg-indigo-600 transition-color
                duration-300 border-indigo-400 hover:border-indigo-300 border-2"
                onClick={submit}><MdSend /></button>
            <div className="bg-indigo-300 text-indigo-950 text-xl font-mono font-extrabold px-2 ml-2 mr-2
                rounded border-indigo-200 border-2">/</div>
            <select name="mode" className="text-xl rounded pb-1 mr-2">
                <option value="off" className="bg-red-600 text-red-50">Off</option>
                <option value="manuel">Manuel</option>
                <option value="failure">Restart Failure</option>
                <option value="always">Restart Always</option>
            </select>
        </div>
    )

}
