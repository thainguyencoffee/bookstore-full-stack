import {ref} from "vue";
import axios from "axios";

interface UserinfoDto {
    username: string
    email: string
    roles: string[]
    exp: number
}

export class User {
    static readonly ANONYMOUS = new User('', '', [])

    constructor(
        readonly name: string,
        readonly email: string,
        readonly roles: string[]
    ) {}

    get isAuthenticated(): boolean {
        return !!this.name;
    }
}

export class UserService {
    readonly current = ref(User.ANONYMOUS);
    private refreshIntervalId?: number;

    constructor() {
        this.refresh();
    }

    async refresh(): Promise<void> {
        if (this.refreshIntervalId) {
            clearInterval(this.refreshIntervalId);
        }
        const response = await axios.get("/bff/api/me");
        const user = response.data as UserinfoDto;
        if (
            user.username !== this.current.value.name ||
            user.email !== this.current.value.email ||
            (user.roles || []).toString() !== this.current.value.roles.toString()
        ) {
            this.current.value = user.username ? new User(user.username, user.email, user.roles || []) : User.ANONYMOUS
        }
        if (user.exp) {
            const now = Date.now();
            const delay = (1000 * user.exp - now) * 0.8;
            if (delay > 2000) {
                this.refreshIntervalId = setInterval(this.refresh, delay);
            }
        }
    }
}