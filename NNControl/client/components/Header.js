import Link from "next/link";
import { MdLogout } from "react-icons/md";

export default function Header({username, admin}) {

    return (
        <div className="p-3 self-start w-full
            bg-indigo-900 flex justify-between items-center">
            <div className="flex items-center">
                <Link href="/">
                    <h1 className="text-indigo-50 font-bold text-3xl pt-1 pb-2 px-3">Nikocraft Console</h1>
                </Link>
                {
                    (admin) ? (
                    <Link href="/admin/user">
                        <div className="text-indigo-50 text-2xl pt-1 pb-2 pl-3
                            underline hover:font-bold">Manage Users</div>
                    </Link>
                    ) : <></>
                }
            </div>
            <div className="flex items-center">
                <Link href="/user">
                    <div className="text-indigo-50 italic text-2xl pt-1 pb-2
                        underline hover:font-bold">{username}</div>
                </Link>
                <Link href="/user/logout">
                    <MdLogout className="text-indigo-50 text-2xl mx-3 hover:scale-125"/>
                </Link>
            </div>
        </div>
    )

}
