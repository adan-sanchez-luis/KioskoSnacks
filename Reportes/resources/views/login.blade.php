<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Login Page</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.0/dist/css/bootstrap.min.css" rel="stylesheet"
        integrity="sha384-gH2yIJqKdNHPEq0n4Mqa/HGKIhSkIHeL5AyhkYV8i59U5AR6csBvApHHNl/vI1Bx" crossorigin="anonymous" />
</head>

<body style="background-color: #999999">
    <div class="container d-flex justify-content-center align-items-center vh-100">
        <div style="display: flex; flex-direction: column;">
            <div>

                @if ($errors->any())
                    <div class="alert alert-danger"
                        style="width: 100%; max-height: 7rem; overflow: hidden; white-space: nowrap;">
                        <ul>
                            @foreach ($errors->all() as $error)
                                <li>{{ $error }}</li>
                            @endforeach
                        </ul>
                    </div>
                @endif
            </div>
            <div>

                <form action="{{ route('signin') }}" method="POST">
                    @csrf
                    <div class="bg-white p-5 rounded-5 text-secondary shadow" style="width: 25rem">
                        <div class="d-flex justify-content-center">
                            <img src="{{ asset('imagenes/logo_rojo.png') }}" alt="logo-icon" style="height: 4rem" />
                        </div>
                        <div class="input-group mt-4">
                            <div class="input-group-text bg-info">
                                <img src="{{ asset('imagenes/user.png') }}" alt="username-icon"
                                    style="height: 1.5rem" />
                            </div>
                            <input name="user" class="form-control bg-light" type="text"
                                placeholder="User name" />
                        </div>
                        <div class="input-group mt-1">
                            <div class="input-group-text bg-info">
                                <img src="{{ asset('imagenes/password.png') }}" alt="password-icon"
                                    style="height: 1.5rem" />
                            </div>
                            <input name="password" class="form-control bg-light" type="password"
                                placeholder="Password" />
                        </div>
                        <div class="input-group mt-1">
                            <div class="input-group-text bg-info">
                                <img src="{{ asset('imagenes/codigo.png') }}" alt="password-icon"
                                    style="height: 1.5rem" />
                            </div>
                            <input name="code" class="form-control bg-light" type="password" placeholder="Code" />
                        </div>
                        <div class="d-flex justify-content-center mt-4">
                            <!-- Agrega la clase d-flex y justify-content-center aquí -->
                            <input type="submit" value="Sing In" class="btn btn-info text-white fw-semibold shadow-sm"
                                style="width: 30%">
                        </div>
                    </div>
                </form>
            </div>
        </div>

    </div>
</body>

</html>
