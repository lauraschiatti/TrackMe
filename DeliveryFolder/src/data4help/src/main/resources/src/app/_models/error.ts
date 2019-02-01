export class Error {
    code: string;  // httpStatusCode + internal error number (40305 => 403 + 05)
    message: string;
}
